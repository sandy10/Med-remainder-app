package com.dosemate.android.data.repository

import com.dosemate.android.data.local.dao.PlaceholderDao
import com.dosemate.android.data.local.entity.PlaceholderEntity
import com.dosemate.android.domain.model.PlaceholderModel
import com.dosemate.android.domain.repository.PlaceholderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of PlaceholderRepository.
 */
class PlaceholderRepositoryImpl @Inject constructor(
    private val dao: PlaceholderDao
) : PlaceholderRepository {

    override fun getPlaceholders(): Flow<List<PlaceholderModel>> {
        return dao.getAll().map { list ->
            list.map { PlaceholderModel(it.id, it.name) }
        }
    }

    override suspend fun addPlaceholder(name: String) {
        dao.insert(PlaceholderEntity(name = name))
    }
}
