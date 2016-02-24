package com.github.groovyclient

import org.json.simple.JSONArray

class QueryResultHandler {
	def res
	def properties

	QueryResultHandler(res,  display, ... displays) {
		this.res = res
		properties = [display]+ displays.flatten()
	}

	def handleResult() {
		if (res instanceof JSONArray || res instanceof List) {
			collectListTypes()
		}
		else {
			properties.collect { property -> res[property] }
		}
	}

	def collectListTypes() {
		res.collect { element ->
			def mapped = properties.collect { property -> element[property] }
			mapped.size == 1 ? mapped[0] : mapped
		}
	}
}
