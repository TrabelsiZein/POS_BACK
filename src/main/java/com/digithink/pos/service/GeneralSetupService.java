package com.digithink.pos.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digithink.pos.model.GeneralSetup;
import com.digithink.pos.repository.GeneralSetupRepository;
import com.digithink.pos.repository._BaseRepository;

@Service
public class GeneralSetupService extends _BaseService<GeneralSetup, Long> {

	@Autowired
	private GeneralSetupRepository generalSetupRepository;

	@Override
	protected _BaseRepository<GeneralSetup, Long> getRepository() {
		return generalSetupRepository;
	}

	public GeneralSetup findByCode(String code) {
		return generalSetupRepository.findByCode(code).orElse(null);
	}

	public String findValueByCode(String code) {
		return generalSetupRepository.findByCode(code).map(GeneralSetup::getValeur).orElse(null);
	}

	@Transactional
	public void updateValue(String code, String value) {
		generalSetupRepository.findByCode(code).ifPresent(setup -> {
			setup.setValeur(value);
			setup.setUpdatedBy("System");
			setup.setUpdatedAt(LocalDateTime.now());
			generalSetupRepository.save(setup);
		});
	}
}
