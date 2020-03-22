package br.com.willianantunes.kotlinplayground.services

import br.com.willianantunes.kotlinplayground.config.DoNotThrowExceptionGivenUnexpectedStatusCode
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
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
        val endpoint = UriComponentsBuilder.fromUri(postEndpoint).build()
        val response = restTemplate.exchange(endpoint.toUri(), HttpMethod.GET, HttpEntity<Unit>(headers), Posts::class.java)

        return when (response.statusCode) {
            HttpStatus.OK -> response.body ?: Posts()
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
}

private class PartnerServiceUnexpectedBehaviorException : RuntimeException()
