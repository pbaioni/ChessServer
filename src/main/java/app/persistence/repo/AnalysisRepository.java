package app.persistence.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.persistence.model.AnalysisDo;

@Repository
public interface AnalysisRepository extends JpaRepository<AnalysisDo, String>{

    public AnalysisDo findByFen(String fen);

	public List<AnalysisDo> findByOnlyPawnsFen(String onlyPawnsFen);

	public void deleteByFen(String fen);
}
