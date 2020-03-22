package br.com.willianantunes.kotlinplayground.services

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.DockerComposeContainer
import java.io.File


@SpringBootTest
class PartnerServiceIT(@Autowired val partnerService: PartnerService) {
    companion object {
        private val instance: KDockerComposeContainer by lazy { defineDockerCompose() }

        class KDockerComposeContainer(file: File) : DockerComposeContainer<KDockerComposeContainer>(file)

        private fun defineDockerCompose() = KDockerComposeContainer(File("docker-compose.yml"))

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            with(instance) {
                withServices("partner-service")
                withLocalCompose(true)
            }.also {
                it.start()
            }
        }

        @AfterAll
        @JvmStatic
        internal fun afterAll() {
            instance.stop()
        }
    }

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
    fun `Should create a new post given valid request`() {
        val postToBeCreated = Post("Eru Iluvatar")
        val createdPost = partnerService.createPost(postToBeCreated)

        assertThat(createdPost).isNotNull()
        assertThat(createdPost.body).isEqualTo(postToBeCreated.body)
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
    fun `Should create a new comment given valid request`() {
        val commentToBeCreated = Comment("Melkor", postId = 1)
        val createdComment = partnerService.createComment(commentToBeCreated)

        assertThat(createdComment).isNotNull()
        assertThat(createdComment.body).isEqualTo(commentToBeCreated.body)
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

    @Test
    fun `Should update comment with new body`() {
        val commentWhichAlreadyExist = partnerService.getCommentById(1)
        commentWhichAlreadyExist!!.body = "A fresh new body to be updated here"
        val updatedComment = partnerService.updateComment(commentWhichAlreadyExist)

        assertThat(updatedComment).isNotNull()
        assertThat(updatedComment.body).isEqualTo(commentWhichAlreadyExist.body)
        assertThat(updatedComment.id).isEqualTo(commentWhichAlreadyExist.id)
    }

    @Test
    fun `Should delete existing comment`() {
        val allComments = partnerService.allComments()
        val sizeOfCommentsBeforeDeletion = allComments.size
        partnerService.deleteComment(allComments.first().id)
        val sizeOfCommentsAfterDeletion = partnerService.allComments().size

        assertThat(sizeOfCommentsAfterDeletion).isLessThan(sizeOfCommentsBeforeDeletion)
    }
}
