import csv
from pyaccumulo import Accumulo, Mutation, Range
from mediawiki import MediaWiki
from random import randint


class DataInjector:

    def __init__(self, accumulo, table_name):
        self.accumulo = accumulo
        self.table_name = table_name
            
    def create_batch_writer(self):
        self.wr = self.accumulo.create_batch_writer(self.table_name)
    
    def create_or_replace_table(self):
        if self.accumulo.table_exists(self.table_name):
            self.accumulo.delete_table(self.table_name)
        self.accumulo.create_table(self.table_name)
        
    def add_mutation(self, mutation):
        self.wr.add_mutation(mutation)

    def write_data(self):
        self.wr.close()

    def close(self):
        self.accumulo.close()


class TsvReader:

    def __init__(self, filename, ignoreFirstLine=True):
        self.filename = filename
        self.reader = open(self.filename, 'r')
        if(ignoreFirstLine):
            next(self.reader)

    def get_line(self):
        return next(self.reader)


## Main 
def create_mutation_from_wikipedia(row, data):
    mutation = Mutation("row)
    mutation.put(cf='title', cq='content', val="%s" % data.title)
    mutation.put(cf='summary', cq='content', val="%s" % data.summary)
    return mutation

def main():
    accumulo = Accumulo(host='172.18.0.1', port=42424, user='root', password='root')
    reader = TsvReader('people.tsv')
    wikipedia = MediaWiki()
    injector = DataInjector(accumulo, 'people')
    injector.create_or_replace_table()
    injector.create_batch_writer()
    for i in range(0, 1000):
        line = reader.get_line()
        p = wikipedia.page(line[1]).summary
        injector.add_mutation(create_mutation_from_wikipedia('famous_people_%d' % i, p))
    injector.write_data()
    injector.close()

main()