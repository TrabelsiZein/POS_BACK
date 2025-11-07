package com.digithink.pos.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.model.GeneralSetup;
import com.digithink.pos.service.GeneralSetupService;

@RestController
@RequestMapping("general-setup")
public class GeneralSetupAPI extends _BaseController<GeneralSetup, Long, GeneralSetupService> {

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody GeneralSetup updatedSetting) {
        try {
            Optional<GeneralSetup> existingOpt = service.findById(id);
            if (!existingOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Setting not found"));
            }

            GeneralSetup existing = existingOpt.get();

            if (Boolean.TRUE.equals(existing.getReadOnly())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("This setting is read only and cannot be modified."));
            }

            existing.setValeur(updatedSetting.getValeur());
            existing.setDescription(updatedSetting.getDescription());

            // Preserve readOnly flag and code
            GeneralSetup saved = service.save(existing);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            String detailedMessage = getDetailedMessage(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(detailedMessage));
        }
    }
}

