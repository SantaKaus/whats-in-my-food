import requests
import json

def main(ingredient):
    TOKEN = "DnMy5UfqPc2iBPYrMehQHN"
    Database_REST_API = 'https://query.dropbase.io/L7jiJyg7XviBS692Gf3jpv'
    Table_to_Query = 'compounds'
    Access_Key = 'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhYmFzZUlkIjoiTDdqaUp5ZzdYdmlCUzY5MkdmM2pwdiIsImFjY2Vzc1Blcm0iOiJmdWxsIiwidG9rZW5JZCI6IlluRjFYOXgyRDU2RGtoWmtTM0RDSWk1Tmp4RWk4bGlBd0ptVUVLN3N0U1Nzb014ZnlvVHNIcEU4M1YxekJXeHEiLCJpYXQiOjE2MTA4NDQ3MTgsImV4cCI6MTYxMTI3NjcxOCwiaXNzIjoiZHJvcGJhc2UuaW8iLCJzdWIiOiJpN2ZuU2N0UEhpdjd0Z0xwUldtV1h3In0.tJLjiezWWOsnKBetnLRl1otOlYxMZPFfnPAU1ebC40c'
    
    
    query = '?name=eq.' + ingredient
    r = requests.get(Database_REST_API + '/' + Table_to_Query + query, headers = {"Authorization": Access_Key})
    return json.loads(r.text)[0]["annotation_quality"]

print(main("Zinc sulfate"))