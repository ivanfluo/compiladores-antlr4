grammar ArrayInt;

//Gramatica
init: '{' value(',' value)* '}' EOF;

value: init
    | INT
    ;

//Lexer
INT: [0-9]+;
WS: [ \t\r\n]+ -> skip;