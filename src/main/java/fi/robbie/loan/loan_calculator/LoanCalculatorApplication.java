package fi.robbie.loan.loan_calculator;

import fi.robbie.loan.loan_calculator.domain.LoanInfo;
import fi.robbie.loan.loan_calculator.domain.LoanInfoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@SpringBootApplication
public class LoanCalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoanCalculatorApplication.class, args);
	}

	@Bean
	public CommandLineRunner initializeLoans(LoanInfoRepository loanInfoRepository) {
		return args -> {
			// Create a loan with simple interest
			LoanInfo simpleLoan = new LoanInfo();
			simpleLoan.setLoanAmount(10000);
			simpleLoan.setInterestRate(10);
			simpleLoan.setLoanTerm(12);
			simpleLoan.setCalculationType("SIMPLE");
			simpleLoan.setLoanName("Simple Interest");
			simpleLoan.setStartDate(LocalDate.now());

			// Create a loan with compound interest
			LoanInfo compoundLoan = new LoanInfo();
			compoundLoan.setLoanAmount(10000);
			compoundLoan.setInterestRate(10);
			compoundLoan.setLoanTerm(12);
			compoundLoan.setCalculationType("COMPOUND");
			compoundLoan.setLoanName("Compound Interest");
			compoundLoan.setStartDate(LocalDate.now());

			// Save both loans to the repository
			loanInfoRepository.save(simpleLoan);
			loanInfoRepository.save(compoundLoan);
		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}