package br.com.willianantunes.kotlinplayground.services

import br.com.willianantunes.kotlinplayground.timeIt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.DockerComposeContainer
import java.io.File
import java.time.Duration


@SpringBootTest
class StockServiceIT(@Autowired val ticketService: StockService) {
    companion object {
        private val instance: KDockerComposeContainer by lazy { defineDockerCompose() }

        class KDockerComposeContainer(file: File) : DockerComposeContainer<KDockerComposeContainer>(file)

        private fun defineDockerCompose() = KDockerComposeContainer(File("docker-compose.yml"))

        @BeforeAll
        @JvmStatic
        internal fun beforeAll() {
            with(instance) {
                withServices("ticker-service")
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
        assertThat(duration).isGreaterThan(Duration.ofSeconds(3))
    }

    @Test
    fun `Should retrieve stock price for GOOG, AMZN and MSFT given ASYNC consult for each`() {
        val duration = timeIt {
            runBlocking {
                val priceForGoogle = async(Dispatchers.IO) { ticketService.getStockPrice("GOOG") }
                val priceForAmazon = async(Dispatchers.IO) { ticketService.getStockPrice("AMZN") }
                val priceForMicrosoft = async(Dispatchers.IO) { ticketService.getStockPrice("MSFT") }

                assertThat(priceForGoogle.await()).isEqualTo("1168.19")
                assertThat(priceForAmazon.await()).isEqualTo("1902.42")
                assertThat(priceForMicrosoft.await()).isEqualTo("112.79")
            }
        }

        println("ASYNC - It took $duration")
        assertThat(duration).isLessThan(Duration.ofSeconds(2))
    }
}
