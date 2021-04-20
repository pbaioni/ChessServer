package app.main.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.io.Files;
import com.google.gson.Gson;

import app.persistence.model.AnalysisDo;
import app.persistence.repo.AnalysisRepository;

@Service
public class DatabaseService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseService.class);

	@Autowired
	AnalysisRepository analysisRepository;

	public void importDbFromFile() throws Exception {

		File dir = new File("./import/");
		File[] dbFiles = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".db");
			}
		});

		File imported = new File("import/imported");
		if (!imported.exists()) {
			imported.mkdirs();
		}

		if(dbFiles.length == 0) {
			LOGGER.info("No database to import");
		}
		
		for (File db : dbFiles) {

			LOGGER.info("Importing db file: " + db.getAbsolutePath());
			FileReader fr = new FileReader(db);
			BufferedReader br = new BufferedReader(fr);
			Iterator<String> linesIterator = br.lines().iterator();

			Gson g = new Gson();
			while (linesIterator.hasNext()) {
				String line = (String) linesIterator.next();
				AnalysisDo pos = g.fromJson(line, AnalysisDo.class);
				analysisRepository.save(pos);
			}

			Files.move(db, new File(imported.getAbsolutePath() + "/" + db.getName()));
			LOGGER.info("File " + db.getName() + " imported and archived");

		}
	}

	public void exportDbToFile() throws Exception {
		
		File exportDir = new File("export");
		if (!exportDir.exists()) {
			exportDir.mkdirs();
		}
		String DATE_FORMAT_NOW = "yyyy-MM-dd_HH-mm-ss";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		String exportTime = sdf.format(cal.getTime());
		File export = new File("export/" + exportTime + ".db");
		
		LOGGER.info("exporting to file " + export.getAbsolutePath());

		List<AnalysisDo> allItems = analysisRepository.findAll();
		Gson g = new Gson();
		FileWriter fw = new FileWriter(export);

		for (AnalysisDo pos : allItems) {
			g.toJson(pos, fw);
			fw.write("\n");
		}

		fw.close();

		LOGGER.info("Database exported");
	}
}
