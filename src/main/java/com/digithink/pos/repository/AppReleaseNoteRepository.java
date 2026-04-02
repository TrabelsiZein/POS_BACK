package com.digithink.pos.repository;

import java.util.List;

import com.digithink.pos.model.AppReleaseNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppReleaseNoteRepository extends JpaRepository<AppReleaseNote, Long> {

    List<AppReleaseNote> findByVersionOrderByIdAsc(String version);
}
