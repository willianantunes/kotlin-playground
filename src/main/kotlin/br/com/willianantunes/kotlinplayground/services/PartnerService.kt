package br.com.willianantunes.kotlinplayground.services

import br.com.willianantunes.kotlinplayground.config.DoNotThrowExceptionGivenUnexpectedStatusCode
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriTemplate
import java.net.URI
import javax.annotation.PostConstruct

/**
 * This service is designed to consume from https://git.io/JviFP
 */
@Service
class PartnerService(
    private val restTemplate: RestTemplate,
    @Value("\${partner-service.post-path}") private val postEndpoint: URI,
    @Value("\${partner-service.comment-path}") private val commentEndpoint: URI
) {
    @PostConstruct
    private fun setup() {
        with(restTemplate) {
            errorHandler = DoNotThrowExceptionGivenUnexpectedStatusCode()
        }
    }

    fun allPosts(): Posts {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val response = restTemplate.exchange(postEndpoint, HttpMethod.GET, HttpEntity<Unit>(headers), Posts::class.java)

        return when (response.statusCode) {
            HttpStatus.OK -> response.body ?: Posts()
            else -> throw PartnerServiceUnexpectedBehaviorException()
        }
    }

    fun createPost(post: Post): Post {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_JSON
        val response = restTemplate.exchange(postEndpoint, HttpMethod.POST, HttpEntity(post, headers), Post::class.java)

        return when (response.statusCode) {
            HttpStatus.CREATED -> {
                println("Post has been created!")
                response.body!!
            }
            else -> throw PartnerServiceUnexpectedBehaviorException()
        }
    }

    fun allComments(): Comments {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val response = restTemplate.exchange(commentEndpoint, HttpMethod.GET, HttpEntity<Unit>(headers), Comments::class.java)

        return when (response.statusCode) {
            HttpStatus.OK -> response.body ?: Comments()
            else -> throw PartnerServiceUnexpectedBehaviorException()
        }
    }

    fun getCommentById(id: Long): Comment? {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val finalAddress = UriTemplate("$commentEndpoint/{commentId}").expand(id)
        val response = restTemplate.exchange(finalAddress, HttpMethod.GET, HttpEntity<Unit>(headers), Comment::class.java)

        return when (response.statusCode) {
            HttpStatus.NOT_FOUND -> null
            HttpStatus.OK -> response.body
            else -> throw PartnerServiceUnexpectedBehaviorException()
        }
    }

    fun createComment(comment: Comment): Comment {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_JSON
        val response = restTemplate.exchange(commentEndpoint, HttpMethod.POST, HttpEntity(comment, headers), Comment::class.java)

        return when (response.statusCode) {
            HttpStatus.CREATED -> {
                println("Comment has been created!")
                response.body!!
            }
            else -> throw PartnerServiceUnexpectedBehaviorException()
        }
    }

    fun updateComment(comment: Comment): Comment {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_JSON
        val finalAddress = UriTemplate("$commentEndpoint/{commentId}").expand(comment.id)
        val response = restTemplate.exchange(finalAddress, HttpMethod.PUT, HttpEntity(comment, headers), Comment::class.java)

        return when (response.statusCode) {
            HttpStatus.OK -> {
                println("Post has been created!")
                response.body!!
            }
            else -> throw PartnerServiceUnexpectedBehaviorException()
        }
    }

    fun deleteComment(id: Long) {
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val finalAddress = UriTemplate("$commentEndpoint/{commentId}").expand(id)
        val response = restTemplate.exchange(finalAddress, HttpMethod.DELETE, HttpEntity<Unit>(headers), Unit::class.java)

        when (response.statusCode) {
            HttpStatus.OK -> println("Comment with ID $id has been deleted!")
            HttpStatus.NOT_FOUND -> throw CommentNotFoundException()
            else -> throw PartnerServiceUnexpectedBehaviorException()
        }
    }
}

private class PartnerServiceUnexpectedBehaviorException : RuntimeException()
private class CommentNotFoundException : RuntimeException()
