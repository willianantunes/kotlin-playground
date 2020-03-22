package br.com.willianantunes.kotlinplayground.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.URI

// This service is designed to consume from https://git.io/JviFP

@Service
class PartnerService(
    private val restTemplate: RestTemplate,
    @Value("\${partner-service.post-path}") private val postEndpoint: URI,
    @Value("\${partner-service.comment-path}") private val commentEndpoint: URI
) {
    fun allPosts(): Posts {
        val entity = restTemplate.getForEntity(postEndpoint, Posts::class.java)

        return entity.body ?: Posts()
    }

    fun allComments(): Comments {
        val entity = restTemplate.getForEntity(commentEndpoint, Comments::class.java)

        return entity.body ?: Comments()
    }
}


