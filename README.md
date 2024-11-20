# 미니 스캐너 & 파서

## 개요
오픈소스 코드를 활용해, 샘플 코드를 스캔 할 수 있는 스캐너 개발 <br>
앞서 만든 스캐너에 파서를 추가해 변수 테이블에 저장 <br>
## 설명
DFA, 코드 리뷰, 회고 등은 각 폴더의 readme.pdf에 있음
## example code

### scanner
```c
Int a, b, sum;
Float x1, y1, zoom;
If (a>b) Then sum = a+b
Else sum = a+10;
while (a ==b) {
  zoom = (sum + x1)/10;
  ch1 = '123';
}
```
```c
int &a1, 2b:
Float x2, y2;
a1 := +100;
X2 = 12.23e+10;
sum = xa123;
```
### parser
```c
var
Int a, b, c;
Float x, y, z;
begin
a = 10; b = 20; c = 3;
x = a + b * c;
print x;
end
```

## 참고 문헌
MinJunKweon님의 Mini-C-Scanner를 기반으로 스캐너에 기능을 추가했고, 스캐너를 활용해서 파서를 구현
