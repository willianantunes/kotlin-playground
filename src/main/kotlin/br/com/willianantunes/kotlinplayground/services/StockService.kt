package br.com.willianantunes.kotlinplayground.services

import br.com.willianantunes.kotlinplayground.config.DoNotThrowExceptionGivenUnexpectedStatusCode
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import javax.annotation.PostConstruct

/**
 * This service is designed to consume from https://git.io/JvDMz
 */
@Service
class StockService(
    private val restTemplate: RestTemplate,
    @Value("\${ticket-service.endpoint}") private val enpoint: URI
) {
    @PostConstruct
    private fun setup() {
        with(restTemplate) {
            errorHandler = DoNotThrowExceptionGivenUnexpectedStatusCode()
        }
    }

    fun getStockPrice(tickerName: String): String {
        val uriComponents = UriComponentsBuilder.fromUri(enpoint).queryParam("ticker", tickerName).build()
        val response = restTemplate.getForEntity(uriComponents.toUri(), String::class.java)

        return when (response.statusCode) {
            HttpStatus.OK -> response.body!!
            else -> throw TicketServiceUnexpectedBehaviorException()
        }
    }
}

private class TicketServiceUnexpectedBehaviorException : RuntimeException()
