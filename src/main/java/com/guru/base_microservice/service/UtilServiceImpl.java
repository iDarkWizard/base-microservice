 package com.guru.base_microservice.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UtilServiceImpl {

	public Sort buildOrder(Map<String, String> order) {
		if (order == null)
			return null;
		Map<String, String> aux = new HashMap<String, String>();
		for (String key : order.keySet()) {
			if (key.startsWith("order[")) {
				aux.put(key.substring(6, key.length() - 1), order.get(key));
			}
		}
		Sort sort = null;
		for (String key : aux.keySet()) {
			if (sort == null) {
				if (aux.get(key).equals("asc"))
					sort = Sort.by(Sort.Direction.ASC, key);
				else
					sort = Sort.by(Sort.Direction.DESC, key);

			} else {
				if (aux.get(key).equals("asc"))
					sort = sort.and(Sort.by(Sort.Direction.ASC, key));
				else
					sort = sort.and(Sort.by(Sort.Direction.DESC, key));

			}
		}
		return sort;
	}

}
