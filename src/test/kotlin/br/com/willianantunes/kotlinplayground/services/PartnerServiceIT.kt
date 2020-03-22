package br.com.willianantunes.kotlinplayground.services

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class PartnerServiceIT(@Autowired val partnerService: PartnerService) {
    @Test
    fun `Should retrieve all posts`() {
        val allPosts = partnerService.allPosts()

        assertThat(allPosts).isNotEmpty()
        assertThat(allPosts).hasSizeGreaterThan(5)
        allPosts.forEach {
            assertThat(it.id).isGreaterThan(0)
            assertThat(it.body).isNotBlank()
        }
    }

    @Test
    fun `Should retrieve all comments`() {
        val allComments = partnerService.allComments()

        assertThat(allComments).isNotEmpty()
        assertThat(allComments).hasSizeGreaterThan(2)
        allComments.forEach {
            assertThat(it.id).isGreaterThan(0)
            assertThat(it.postId).isGreaterThan(0)
            assertThat(it.body).isNotBlank()
        }
    }

    @Test
    fun `Should retrieve specific comment given its ID`() {
        val comment = partnerService.getCommentById(1)

        assertThat(comment).isNotNull()

        comment?.let {
            assertThat(it.id).isEqualTo(1)
            assertThat(it.postId).isEqualTo(1)
            assertThat(it.body).isEqualTo("baz")
        }
    }

    @Test
    fun `Should retrieve null comment given its ID wasn't found`() {
        val comment = partnerService.getCommentById(500)

        assertThat(comment).isNull()
    }
}
