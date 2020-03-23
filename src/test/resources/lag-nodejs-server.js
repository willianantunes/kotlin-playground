const http = require('http')
const url = require('url')
const querystring = require('querystring')

const timeToBeDelayed = parseInt(process.env.DELAY_ANSWER_MS || 1000)
console.log(`Time to be delayed in each request: ${timeToBeDelayed}`)
const fakeDatabaseOfStockPrices = new Map([['GOOG', 1168.19], ['AMZN', 1902.42], ['MSFT', 112.79]])
const delayResponseToSimulateDelayedConnection = (timeOutBeforeExecution, logic) => setTimeout(logic, timeOutBeforeExecution);

(async function () {
  const handleRequests = function (request, response) {
    const params = querystring.parse(url.parse(request.url).query)
    const symbol = params.ticker
    console.log(`Received ticker: ${symbol}`)

    const responseWriter = (status, message = '') => {
      response.writeHead(status, { 'Content-Type': 'text/plain' })
      return response.end(message.toString())
    }

    const mainLogic = () => {
      if (fakeDatabaseOfStockPrices.has(symbol)) {
        const priceFound = fakeDatabaseOfStockPrices.get(symbol)
        console.log(`It's available! Returning ${priceFound}`)
        return responseWriter(200, priceFound)
      }
      console.log(`We don't have nothing for ${symbol}`)
      return responseWriter(404)
    }

    delayResponseToSimulateDelayedConnection(timeToBeDelayed, mainLogic)
  }

  const server = http.createServer(handleRequests)
  const portToRunServer = process.env.PORT || 8085
  await server.listen(portToRunServer)
  console.log(`Now you can consult me on port ${portToRunServer}!`)
})()
