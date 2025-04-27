package fi.robbie.loan.loan_calculator.web;

import fi.robbie.loan.loan_calculator.domain.Payment;
import fi.robbie.loan.loan_calculator.service.LoanService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoanRestController {

    private final LoanService loanService;

    public LoanRestController(LoanService loanService) {
        this.loanService = loanService;
    }

    /**
     * Endpoint to calculate and return the loan payment schedule.
     *
     * @param principal       The loan amount.
     * @param annualRate      The annual interest rate in percentages.
     * @param months          The loan term in months.
     * @param calculationType The type of interest calculation ("SIMPLE" or "COMPOUND").
     * @return A list of Payment objects representing the payment schedule.
     */
    @GetMapping("/loan-schedule")
    public List<Payment> getLoanSchedule(@RequestParam double principal, @RequestParam double annualRate, @RequestParam int months, @RequestParam String calculationType) {

        // Delegate calculation to LoanService
        List<Payment> schedule = loanService.calculateLoanSchedule(principal, annualRate, months, calculationType);

        return schedule;
    }
}