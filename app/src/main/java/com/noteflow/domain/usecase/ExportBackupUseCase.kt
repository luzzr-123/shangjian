package com.luuzr.jielv.domain.usecase

import com.luuzr.jielv.domain.repository.BackupOperationResult
import com.luuzr.jielv.domain.repository.BackupRepository
import javax.inject.Inject

class ExportBackupUseCase @Inject constructor(
    private val backupRepository: BackupRepository,
) {
    suspend operator fun invoke(destinationUri: String): BackupOperationResult {
        return backupRepository.exportBackup(destinationUri)
    }
}
