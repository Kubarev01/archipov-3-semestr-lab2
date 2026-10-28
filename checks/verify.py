import json
from pathlib import Path
from urllib.request import Request, urlopen
from urllib.error import HTTPError
from datetime import datetime
root = Path(__file__).resolve().parent
out = root.parent / 'evidence' / 'responses'
out.mkdir(parents=True, exist_ok=True)
cases = json.loads((root / 'cases.json').read_text())
for i, case in enumerate(cases, 1):
    raw = case['body'] if isinstance(case['body'], str) else json.dumps(case['body'], ensure_ascii=False)
    request = Request('http://localhost:8082/feedback', data=raw.encode(), headers={'Content-Type': 'application/json'}, method='POST')
    try:
        response = urlopen(request)
    except HTTPError as error:
        response = error
    body = json.loads(response.read().decode())
    assert response.status == case['status'], (case['name'], response.status, body)
    assert set(body) == {'uid', 'operationUid', 'systemTime', 'code', 'errorCode', 'errorMessage'}
    assert body['code'] == ('success' if case['status'] == 200 else 'failed')
    assert body['errorCode'] == case['error'], (case['name'], body)
    datetime.fromisoformat(body['systemTime'].replace('Z', '+00:00'))
    for fragment in case['contains']:
        assert fragment in body['errorMessage'], (fragment, body)
    (out / f'{i:02}.json').write_text(json.dumps({'test': case['name'], 'request': case['body'], 'httpStatus': response.status, 'response': body}, ensure_ascii=False, indent=2))
    print(f"PASS {response.status} {case['name']} => {body['errorCode'] or 'success'}")
print(f'{len(cases)} HTTP checks passed')
