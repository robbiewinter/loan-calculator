package fi.robbie.loan.loan_calculator.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanInfoRepository extends JpaRepository<LoanInfo, Long> {
}
