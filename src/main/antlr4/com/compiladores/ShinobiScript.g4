grammar ShinobiScript;

// Gramatica o Producciones
expresion
    : expresion op=(MULT | DIV) expresion
    | expresion op=(PLUS | MINUS) expresion
    | expresion op=(LT | GT | LEQ | GEQ) expresion
    | expresion op=(EQ | NEQ) expresion
    | expresion op=(AND | OR) expresion
    | NOT expresion
    | LPAREN expresion RPAREN
    | literal
    | ID
    ;

declaracion
    : tipo ID SEMI // chakra;
    ;

asignacion
    : tipo ID ASSIGN literal SEMI // tipo nombre = valor;
    ;

// literales
literal
    : DOUBLE
    | INT // chakra edad = 18;
    | CHAR
    | STRING
    | MARU // shinri verdadero = maru
    | BATSU // shinri falso = batsu
    ;

// tipos
tipo
    : CHAKRA
    | RYO
    | KANA
    | SHINRI
    | MOJI
    ;

// Lexer
// Token        : Lexema
CHAKRA          : 'chakra';         // int
RYO             : 'ryo';            // double
KANA            : 'kana';           // char
SHINRI          : 'shinri';         // boolean
MOJI            : 'moji';           // string

MOSHI           : 'moshi';          // if
SORE            : 'sore';           // else
MATA            : 'mata';           // elseif
NARA            : 'nara';           // then

KURIKAE         : 'kurikae';        // for
NAGARA          : 'nagara';         // while
SURU            : 'suru';           // do while

MU              : 'mu';             // void
MARU            : 'maru';           // true
BATSU           : 'batsu';          // false

KAKEMONO        : 'kakemono';       // main
JUTSU           : 'jutsu';          // function
KUCHIYOSE       : 'kuchiyose';      // return
KAI             : 'kai';            // print

LPAREN          : '(';
RPAREN          : ')';
LBRACE          : '{';
RBRACE          : '}';
SEMI            : ';';
COMMA           : ',';
ASSIGN          : '=';

PLUS            : '+';
MINUS           : '-';
MULT            : '*';
DIV             : '/';

LEQ             : '<=';
GEQ             : '>=';
EQ              : '==';
NEQ             : '!=';

LT              : '<';
GT              : '>';

AND             : '&&';
OR              : '||';
NOT             : '!';

DOUBLE          : [0-9]+ '.' [0-9]+;
INT             : [0-9]+;
CHAR            : '\'' (~['\\] | '\\' .) '\'';
STRING          : '"' (~["\\] | '\\' . )* '"';

ID              : [a-zA-Z_][a-zA-Z0-9_]*;
WS              : [ \t\r\n]+ -> skip;

LINE_COMMENT    : '//' ~[\r\n]* -> skip;
BLOCK_COMMENT   : '/*' .*? '*/' -> skip;