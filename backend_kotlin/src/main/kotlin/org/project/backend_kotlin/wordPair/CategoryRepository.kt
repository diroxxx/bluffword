package org.project.backend_kotlin.wordPair

import org.project.backend_kotlin.model.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long> {



}