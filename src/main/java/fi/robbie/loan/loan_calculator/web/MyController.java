package fi.robbie.loan.loan_calculator.web;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import fi.robbie.loan.loan_calculator.domain.LoanInfo;
import fi.robbie.loan.loan_calculator.domain.LoanInfoRepository;
import fi.robbie.loan.loan_calculator.domain.Payment;
import fi.robbie.loan.loan_calculator.domain.User;
import fi.robbie.loan.loan_calculator.domain.UserRepository;
import fi.robbie.loan.loan_calculator.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
public class MyController {

    private static final Logger logger = LoggerFactory.getLogger(MyController.class);

    private final LoanInfoRepository loanInfoRepository;
    private final LoanService loanService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public MyController(LoanInfoRepository loanInfoRepository, LoanService loanService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.loanInfoRepository = loanInfoRepository;
        this.loanService = loanService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Handles GET requests to the root URL ("/")
    @GetMapping("/loan-form")
    public String showLoanForm(Model model) {
        model.addAttribute("loanInfo", new LoanInfo());
        return "loan-form";
    }

    // Handles POST requests to submit the loan form
    @PostMapping("/submit-loan")
    public String submitLoanForm(@ModelAttribute LoanInfo loanInfo) {
        // Retrieve the currently logged-in user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        // Fetch the user entity from the database
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new IllegalStateException("Logged-in user not found in the database");
        }

        // Associate the loan with the logged-in user
        loanInfo.setUser(currentUser);

        // Save the loan
        loanInfoRepository.save(loanInfo);
        return "redirect:/loan-list";
    }

    // Handles GET requests to display the list of loans
    @GetMapping("/loan-list")
    public String showLoanList(Model model) {
        // Retrieve the currently logged-in user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        // Fetch the user entity from the database
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new IllegalStateException("Logged-in user not found in the database");
        }

        // Fetch loans associated with the logged-in user
        List<LoanInfo> loans = loanInfoRepository.findByUser(currentUser);
        model.addAttribute("loans", loans);
        return "loan-list";
    }

    // Handles GET requests to show the edit form for a loan
    @GetMapping("/loan-edit/{id}")
    public String showEditLoanForm(@PathVariable Long id, Model model) {
        LoanInfo loanInfo = loanInfoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid loan ID: " + id));
        model.addAttribute("loanInfo", loanInfo);
        return "loan-form";
    }

    // Handles GET requests to show detailed information about a specific loan
    @GetMapping("/loan-details/{id}")
    public String showLoanDetails(@PathVariable Long id, Model model) {
        LoanInfo loanInfo = loanInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid loan ID: " + id));
        model.addAttribute("loanInfo", loanInfo);

        // Calculate loan schedule using LoanService
        double principal = loanInfo.getLoanAmount();
        double annualRate = loanInfo.getInterestRate();
        int months = loanInfo.getLoanTerm();
        String calculationType = loanInfo.getCalculationType(); // Use the calculation type from LoanInfo

        List<Payment> loanSchedule = loanService.calculateLoanSchedule(principal, annualRate, months, calculationType);

        model.addAttribute("loanSchedule", loanSchedule);
        return "loan-details";
    }

    // Handles POST requests to delete a loan
    @PostMapping("/loan-delete/{id}")
    public String deleteLoan(@PathVariable Long id) {
        loanInfoRepository.deleteById(id);
        return "redirect:/loan-list";
    }

    // Handles POST requests to apply an extra payment to a loan
    @PostMapping("/loan-extra-payment/{id}")
    public String applyExtraPayment(@PathVariable Long id, @RequestParam double extraPayment) {
        LoanInfo loanInfo = loanInfoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid loan ID: " + id));
        loanInfo.setLoanAmount(loanInfo.getLoanAmount() - extraPayment); // Reduce the loan amount by the extra payment
        loanInfoRepository.save(loanInfo);
        return "redirect:/loan-details/" + id;
    }

    // Handles GET requests to show the login page
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Handles GET requests to show the registration page
    @GetMapping("/register")
    public String register(Model model) {
        logger.info("Accessing the registration page.");
        model.addAttribute("user", new User()); // Ensure a User object is added to the model
        return "register";
    }

    // Handles POST requests to register a new user
    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, @RequestParam String email) {
        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            userRepository.save(user);
        } catch (Exception e) {
            return "register";
        }

        return "redirect:/login";
    }
}
