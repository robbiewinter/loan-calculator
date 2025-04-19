package fi.robbie.loan.loan_calculator.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import fi.robbie.loan.loan_calculator.domain.LoanInfo;
import fi.robbie.loan.loan_calculator.domain.LoanInfoRepository;
import fi.robbie.loan.loan_calculator.domain.Payment;
import fi.robbie.loan.loan_calculator.service.LoanService;

import java.util.List;

@Controller
public class MyController {

    private final LoanInfoRepository loanInfoRepository;
    private final LoanService loanService;

    public MyController(LoanInfoRepository loanInfoRepository, LoanService loanService) {
        this.loanInfoRepository = loanInfoRepository;
        this.loanService = loanService;
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
        loanInfoRepository.save(loanInfo);
        return "redirect:/loan-list";
    }

    // Handles GET requests to display the list of loans
    @GetMapping("/loan-list")
    public String showLoanList(Model model) {
        List<LoanInfo> loans = loanInfoRepository.findAll();

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
}
