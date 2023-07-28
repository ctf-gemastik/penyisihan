package id.co.gemastik.web.webtool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import id.co.gemastik.web.webtool.model.AccountModel;

public interface AccountRepository extends JpaRepository<AccountModel, Object>{
	AccountModel findByUsername(String username);
	AccountModel findById(Integer id);
}
