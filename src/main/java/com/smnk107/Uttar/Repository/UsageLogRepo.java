package com.smnk107.Uttar.Repository;

import com.smnk107.Uttar.Entity.UsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsageLogRepo extends JpaRepository<UsageLog,Long> {
}
