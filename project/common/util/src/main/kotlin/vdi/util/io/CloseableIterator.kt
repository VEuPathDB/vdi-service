package vdi.util.io

import java.io.Closeable

interface CloseableIterator<T>: Iterator<T>, Closeable