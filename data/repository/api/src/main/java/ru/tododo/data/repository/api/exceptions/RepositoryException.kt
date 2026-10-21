package ru.tododo.data.repository.api.exceptions

import ru.tododo.data.repository.api.ErrorCode

class RepositoryException(val errorCode: ErrorCode): Exception()