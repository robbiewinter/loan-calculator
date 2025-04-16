package fi.robbie.loan.loan_calculator.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import fi.robbie.loan.loan_calculator.domain.LoanInfo;
import fi.robbie.loan.loan_calculator.domain.LoanInfoRepository;

import java.util.List;

@Controller
public class MyController {

    private final LoanInfoRepository loanInfoRepository;

    public MyController(LoanInfoRepository loanInfoRepository) {
        this.loanInfoRepository = loanInfoRepository;
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
}
