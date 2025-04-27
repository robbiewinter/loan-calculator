package fi.robbie.loan.loan_calculator.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanInfoRepository extends JpaRepository<LoanInfo, Long> {
    List<LoanInfo> findByUser(User user);
}
