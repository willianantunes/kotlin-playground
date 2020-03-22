package br.com.willianantunes.kotlinplayground.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriTemplate
import java.net.URI

// This service is designed to consume from https://git.io/JviFP

@Service
class PartnerService(
    private val restTemplate: RestTemplate,
    @Value("\${partner-service.post-path}") private val postEndpoint: URI,
    @Value("\${partner-service.comment-path}") private val commentEndpoint: URI
) {
    fun allPosts(): Posts {
        val response = restTemplate.getForEntity(postEndpoint, Posts::class.java)

        return response.body ?: Posts()
    }

    fun allComments(): Comments {
        val response = restTemplate.getForEntity(commentEndpoint, Comments::class.java)

        return response.body ?: Comments()
    }

    fun getCommentById(id: Long): Comment? {
        val finalAddress = UriTemplate("$commentEndpoint/{commentId}").expand(id)
        val response = restTemplate.getForEntity(finalAddress, Comment::class.java)

        return response.body
    }
}


