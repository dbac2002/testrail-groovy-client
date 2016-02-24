package com.github.groovyclient

import org.json.simple.JSONArray

class QueryResultHandler {
	def res
	def properties

	QueryResultHandler(res,  display, ... displays) {
		this.res = res
		properties = [display]+ displays.flatten()
	}

	def handleResult(action, baseurl="") {
		if (res instanceof JSONArray || res instanceof List) {
			collectListTypes(action, baseurl)
		}
		else {
			properties.collect { property -> res[property] }
		}
	}

	def collectListTypes(action, baseurl) {
		res.collect { element ->
			def mapped = properties.collect { property -> element[property] }
			if (baseurl && action.link) {
				mapped << baseurl + action.link + element['id']
			}
			mapped.size == 1 ? mapped[0] : mapped
		}
	}
}
