package com.rebecca

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.event.EventListener

/**
 *	A basic web-server.
 *
 * 	@author Rebecca Davies <email@rebecca.sh>
 * 	@since 2022-03-22
 */

@SpringBootApplication
class WebApp

fun main(args: Array<String>) {
	runApplication<WebApp>(*args)
}
