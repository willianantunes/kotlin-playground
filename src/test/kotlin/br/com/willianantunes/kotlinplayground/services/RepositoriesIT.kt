package br.com.willianantunes.kotlinplayground.services

import br.com.willianantunes.kotlinplayground.business.Article
import br.com.willianantunes.kotlinplayground.business.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull

@DataJpaTest
class RepositoriesIT @Autowired constructor(
    val entityManager: TestEntityManager,
    val userRepository: UserRepository,
    val articleRepository: ArticleRepository
) {
    @Test
    fun `Should return article from findByIdOrNull given some was persisted`() {
        val jafar = User("jafar-vizier", "jafar", "vizier")
        entityManager.persist(jafar)
        val article = Article("I hate Agrabah", "Dear Iago Parrot...", "Lorem ipsum", author = jafar)
        entityManager.persist(article)
        entityManager.flush()

        val articleFound = articleRepository.findByIdOrNull(article.id!!)
        assertThat(articleFound).isEqualTo(article)
    }

    @Test
    fun `Should return User given he was persisted previously`() {
        val iago = User("iago", "Iago", "Parrot")
        entityManager.persist(iago)
        entityManager.flush()

        val userFound = userRepository.findByLogin(iago.login)
        assertThat(userFound).isEqualTo(iago)
    }

    @Test
    fun `Should find by article slug`() {
        val jafar = User("jafar-vizier", "jafar", "vizier")
        entityManager.persist(jafar)
        val article = Article("I hate Agrabah", "Dear Iago Parrot...", "Lorem ipsum", author = jafar)
        entityManager.persist(article)
        entityManager.flush()


        val articleFound = articleRepository.findBySlug("i-hate-agrabah")
        assertThat(articleFound).isEqualTo(article)
    }
}
