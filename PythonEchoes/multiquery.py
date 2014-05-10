import privateresources as PrivateResource

import urllib2
import urllib
import json
import time

def extractTextMessages():
	multiquery = "SELECT body FROM message WHERE thread_id IN  (SELECT thread_id FROM thread WHERE folder_id = 1) AND author_id = me() ORDER BY created_time"
	s = executeFQL(multiquery, PrivateResource.accessToken, True)
	print s
	start = time.time()
	return sum([d[u'body'].encode('ascii', 'replace').split() + ['.'] for d in s],[])

def runMessageDemo():
	multiquery = "SELECT body FROM message WHERE thread_id IN  (SELECT thread_id FROM thread WHERE folder_id = 1) AND author_id = me() ORDER BY created_time"
	s = executeFQL(multiquery, PrivateResource.accessToken, True)
	print s

def executeFQL(query, accessToken, jsonFormat = False):
	res = None
	try:
		url = "https://api.facebook.com/method/fql.query?query=" + urllib.quote(query)
		if jsonFormat:
			url = url + "&format=json"
		if accessToken:
			url = url + "&access_token=" + accessToken
		inp = urllib2.urlopen(url)
		res = json.load(inp) if jsonFormat else ''.join(inp.readlines())
		inp.close()
	except:
		print "FB_ERROR:", sys.exc_info()[0]
	return res
