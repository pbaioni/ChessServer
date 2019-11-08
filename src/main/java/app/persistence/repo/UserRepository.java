package app.persistence.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.persistence.model.UserDo;

@Repository
public interface UserRepository extends JpaRepository<UserDo, String>{

    public UserDo findByUserName(String userName);

	public List<UserDo> findByPassword(String password);
	
	public List<UserDo> findByLastLogin(Date lastLogin);

	public void deleteByUserName(String userName);
}
