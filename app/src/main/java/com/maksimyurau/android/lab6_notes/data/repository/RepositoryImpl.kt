package com.maksimyurau.android.lab6_notes.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.maksimyurau.android.lab6_notes.data.database.dao.ColorDao
import com.maksimyurau.android.lab6_notes.data.database.dao.NoteDao
import com.maksimyurau.android.lab6_notes.data.database.dbmapper.DbMapper
import com.maksimyurau.android.lab6_notes.data.database.model.ColorDbModel
import com.maksimyurau.android.lab6_notes.data.database.model.NoteDbModel
import com.maksimyurau.android.lab6_notes.model.ColorModel
import com.maksimyurau.android.lab6_notes.model.NoteModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * {@inheritDoc}
 */
class RepositoryImpl(
  private val noteDao: NoteDao,
  private val colorDao: ColorDao,
  private val dbMapper: DbMapper
) : Repository {

  private val notesNotInTrashLiveData: MutableLiveData<List<NoteModel>> by lazy {
    MutableLiveData<List<NoteModel>>()
  }

  private val notesInTrashLiveData: MutableLiveData<List<NoteModel>> by lazy {
    MutableLiveData<List<NoteModel>>()
  }

  init {
    initDatabase(this::updateNotesLiveData)
  }

  /**
   * Заполняет базу данных цветами, если она пуста.
   */
  private fun initDatabase(postInitAction: () -> Unit) {
    GlobalScope.launch {
      // Prepopulate colors
      val colors = ColorDbModel.DEFAULT_COLORS.toTypedArray()
      val dbColors = colorDao.getAllSync()
      if (dbColors.isNullOrEmpty()) {
        colorDao.insertAll(*colors)
      }

      // Prepopulate notes
      val notes = NoteDbModel.DEFAULT_NOTES.toTypedArray()
      val dbNotes = noteDao.getAllSync()
      if (dbNotes.isNullOrEmpty()) {
        noteDao.insertAll(*notes)
      }

      postInitAction.invoke()
    }
  }

  override fun getAllNotesNotInTrash(): LiveData<List<NoteModel>> = notesNotInTrashLiveData

  override fun getAllNotesInTrash(): LiveData<List<NoteModel>> = notesInTrashLiveData

  private fun getAllNotesDependingOnTrashStateSync(inTrash: Boolean): List<NoteModel> {
    val colorDbModels: Map<Long, ColorDbModel> = colorDao.getAllSync().map { it.id to it }.toMap()
    val dbNotesNotInTrash: List<NoteDbModel> =
      noteDao.getAllSync().filter { it.isInTrash == inTrash }
    return dbMapper.mapNotes(dbNotesNotInTrash, colorDbModels)
  }

  override fun getNote(id: Long): LiveData<NoteModel> =
    Transformations.map(noteDao.findById(id)) {
      val colorDbModel = colorDao.findByIdSync(it.colorId)
      dbMapper.mapNote(it, colorDbModel)
    }

  override fun insertNote(note: NoteModel) {
    noteDao.insert(dbMapper.mapDbNote(note))
    updateNotesLiveData()
  }

  override fun deleteNote(id: Long) {
    noteDao.delete(id)
    updateNotesLiveData()
  }

  override fun deleteNotes(noteIds: List<Long>) {
    noteDao.delete(noteIds)
    updateNotesLiveData()
  }

  override fun moveNoteToTrash(noteId: Long) {
    val dbNote = noteDao.findByIdSync(noteId)
    val newDbNote = dbNote.copy(isInTrash = true)
    noteDao.insert(newDbNote)
    updateNotesLiveData()
  }

  override fun restoreNotesFromTrash(noteIds: List<Long>) {
    val dbNotesInTrash = noteDao.getNotesByIdsSync(noteIds)
    dbNotesInTrash.forEach {
      val newDbNote = it.copy(isInTrash = false)
      noteDao.insert(newDbNote)
    }
    updateNotesLiveData()
  }

  override fun getAllColors(): LiveData<List<ColorModel>> =
    Transformations.map(colorDao.getAll()) { dbMapper.mapColors(it) }

  override fun getAllColorsSync(): List<ColorModel> = dbMapper.mapColors(colorDao.getAllSync())

  override fun getColor(id: Long): LiveData<ColorModel> =
    Transformations.map(colorDao.findById(id)) { dbMapper.mapColor(it) }

  override fun getColorSync(id: Long): ColorModel = dbMapper.mapColor(colorDao.findByIdSync(id))

  private fun updateNotesLiveData() {
    notesNotInTrashLiveData.postValue(getAllNotesDependingOnTrashStateSync(false))
    val newNotesInTrashLiveData = getAllNotesDependingOnTrashStateSync(true)
    notesInTrashLiveData.postValue(newNotesInTrashLiveData)
  }
}
