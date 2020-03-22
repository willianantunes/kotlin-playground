package br.com.willianantunes.kotlinplayground.services

import br.com.willianantunes.kotlinplayground.timeIt
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class StockServiceIT(@Autowired val ticketService: StockService) {
    @Test
    fun `Should retrieve stock price for GOOG, AMZN and MSFT given SYNC one consult at a time`() {
        val duration = timeIt {
            val priceForGoogle = ticketService.getStockPrice("GOOG")
            val priceForAmazon = ticketService.getStockPrice("AMZN")
            val priceForMicrosoft = ticketService.getStockPrice("MSFT")

            assertThat(priceForGoogle).isEqualTo("1168.19")
            assertThat(priceForAmazon).isEqualTo("1902.42")
            assertThat(priceForMicrosoft).isEqualTo("112.79")
        }

        println("SYNC - It took $duration")
    }

    @Test
    fun `Should retrieve stock price for GOOG, AMZN and MSFT given ASYNC consult for each`() {
        val duration = timeIt {
            runBlocking {
                val priceForGoogle = async { ticketService.getStockPrice("GOOG") }
                val priceForAmazon = async { ticketService.getStockPrice("AMZN") }
                val priceForMicrosoft = async { ticketService.getStockPrice("MSFT") }

                assertThat(priceForGoogle.await()).isEqualTo("1168.19")
                assertThat(priceForAmazon.await()).isEqualTo("1902.42")
                assertThat(priceForMicrosoft.await()).isEqualTo("112.79")
            }
        }

        println("ASYNC - It took $duration")
    }
}
