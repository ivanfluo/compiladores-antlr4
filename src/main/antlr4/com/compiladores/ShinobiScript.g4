grammar ShinobiScript;

init
    : (declaracionFuncion)* main EOF
    ;

main
    : KAKEMONO LPAREN RPAREN bloque
    ;

declaracionFuncion
    : JUTSU (tipo | MU) ID LPAREN parametros RPAREN bloque
    ;

parametros
    : tipo ID (COMMA tipo ID)*
    ;

bloque
    : LBRACE sentencia* RBRACE
    ;

// generacion de sentencias (como termina)
sentencia
    : declaracion SEMI
    | asignacion SEMI
    | sentenciaControl
    | sentenciaIterativa
    | llamadaFuncion SEMI
    | impresion SEMI
    | retorno SEMI
    | bloque
    ;

sentenciaControl
    : declaracionIf
    | declaracionSwitch
    ;

sentenciaIterativa
    : declaracionFor
    | declaracionWhile
    | declaracionDoWhile SEMI
    ;

// generacion de declaraciones (que hace)
declaracionSwitch
    : HENKA LPAREN expresion RPAREN LBRACE
        declaracionCase+ declaracionDefault?
    RBRACE
    ;

declaracionCase
    : REI literal COLON sentencia* KOWASU SEMI
    ;

declaracionDefault
    : KYUBI COLON sentencia* (KOWASU SEMI)?
    ;

declaracionIf
    : MOSHI LPAREN expresion RPAREN bloque (MATA LPAREN expresion RPAREN bloque)* (SORE bloque)?
    ;

declaracionWhile
    : NAGARA LPAREN expresion RPAREN bloque
    ;

declaracionDoWhile
    : SURU bloque NAGARA LPAREN expresion RPAREN SEMI
    ;

declaracionFor
    : KURIKAE LPAREN (declaracion | asignacion)? SEMI expresion SEMI asignacion RPAREN bloque
    ;

declaracion
    : tipo ID (ASSIGN expresion)? // chakra nombre = valor ;
    ;

asignacion
    : ID ASSIGN expresion // nombre = valor;
    ;

impresion
    : KAI LPAREN expresion RPAREN
    ;

retorno
    : KUCHIYOSE expresion?
    ;

// Gramatica o Producciones
expresion
    : LPAREN expresion RPAREN
    | NOT expresion
    | expresion op=(MULT | DIV) expresion
    | expresion op=(PLUS | MINUS) expresion
    | expresion op=(LT | GT | LEQ | GEQ) expresion
    | expresion op=(EQ | NEQ) expresion
    | expresion op=(AND | OR) expresion
    | llamadaFuncion
    | ID
    | literal
    ;

//llamda a funciones
llamadaFuncion
    : ID LPAREN (expresion (COMMA expresion)*)? RPAREN
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

HENKA           : 'henka';          // switch
REI             : 'rei';            // case
KOWASU          : 'kowasu';         // break
KYUBI           : 'kiyubi';         // default

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

LPAREN          : 'LPAREN(';
RPAREN          : ')RPAREN';
LBRACE          : 'LBRACE{';
RBRACE          : '}RBRACE';
SEMI            : ';SEMI';
COMMA           : ',COMMA';
ASSIGN          : '=ASSIGN';
COLON           : ':COLON';

PLUS            : '+PLUS';
MINUS           : '-MINUS';
MULT            : '*MULT';
DIV             : '/DIV';

LEQ             : '<=LEQ';
GEQ             : '>=GEQ';
EQ              : '==EQ';
NEQ             : '!=NEQ';

LT              : '<LT';
GT              : '>GT';

AND             : '&&AND';
OR              : '||OR';
NOT             : '!NOT';

DOUBLE          : [0-9]+ '.' [0-9]+;
INT             : [0-9]+;
CHAR            : '\'' (~['\\] | '\\' .) '\'';
STRING          : '"' (~["\\] | '\\' . )* '"';

ID              : [a-zA-Z_][a-zA-Z0-9_]*;
WS              : [ \t\r\n]+ -> skip;

LINE_COMMENT    : '//' ~[\r\n]* -> skip;
BLOCK_COMMENT   : '/*' .*? '*/' -> skip;