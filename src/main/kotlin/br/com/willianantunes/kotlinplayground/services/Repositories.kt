package br.com.willianantunes.kotlinplayground.services

import br.com.willianantunes.kotlinplayground.business.Article
import br.com.willianantunes.kotlinplayground.business.User
import org.springframework.data.repository.CrudRepository

interface ArticleRepository : CrudRepository<Article, Long> {
    fun findBySlug(slug: String): Article?
    fun findAllByOrderByAddedAtDesc(): Iterable<Article>
}

interface UserRepository : CrudRepository<User, Long> {
    fun findByLogin(login: String): User?
}
