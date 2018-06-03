package pl.kacperduras.skinfetcher.mock

class MockClosable: AutoCloseable {

  var closed = false

  override fun close() {
    this.closed = true
  }

}
