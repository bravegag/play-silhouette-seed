for i in range(100):
        filename = "public/html/indexable/en/page-%d.html" % i
        content = "<html><head><title>This is a super page %d</title></head><body><h1>Hello, World!</h1>This is a content %d of html page to test</body></html>" %(i, i)
	f= open(filename, "w+")
	f.write(content)
	f.close()
