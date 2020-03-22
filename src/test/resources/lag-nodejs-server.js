(async function () {
  var http = require('http')
  var url = require('url')
  var querystring = require('querystring')

  var handler = function (request, response) {
    var params = querystring.parse(url.parse(request.url).query)

    var write = function (status, message) {
      response.writeHead(status, { 'Content-Type': 'text/plain' })
      return response.end(message)
    }

    var symbol = params.ticker

    const prices = new Map([['GOOG', 1168.19], ['AMZN', 1902.42], ['MSFT', 112.79]])

    setTimeout(() => {
      if (prices.has(symbol)) {
        return write(200, '' + prices.get(symbol))
      }

      return write(404, '')
    }, 1000)
  }

  const server = http.createServer(handler)
  await server.listen(8085)
  console.log('Now you can consult me!')
}).call(this)
