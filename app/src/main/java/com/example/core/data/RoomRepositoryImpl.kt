package com.example.core.data

import com.example.data.TodoDao
import com.example.data.TodoItem
import com.example.data.University
import com.example.data.UniversityDao
import kotlinx.coroutines.flow.Flow

class UniversityRepositoryImpl(
    private val dao: UniversityDao
) : IUniversityRepository {
    override fun getAllUniversities(): Flow<List<University>> = dao.getAll()
    override fun getActiveUniversities(): Flow<List<University>> = dao.getAllActive()
    override suspend fun getUniversityById(id: Int): University? = dao.getById(id)
    override suspend fun insertUniversity(university: University): Long = dao.insert(university)
    override suspend fun updateUniversity(university: University) = dao.update(university)
    override suspend fun deleteUniversity(university: University) = dao.delete(university)
}

class TodoRepositoryImpl(
    private val dao: TodoDao
) : ITodoRepository {
    override fun getAllTodos(): Flow<List<TodoItem>> = dao.getAllTodos()
    override suspend fun insertTodo(todo: TodoItem) = dao.insert(todo)
    override suspend fun updateTodo(todo: TodoItem) = dao.update(todo)
    override suspend fun deleteTodo(todo: TodoItem) = dao.delete(todo)
}
