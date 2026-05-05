grammar ShinobiScript;

init
    : (declaracionFuncion)* main EOF
    ;

main
    : KAKEMONO LPAREN RPAREN bloque
    ;

declaracionFuncion
    : JUTSU (tipo | MU) ID LPAREN parametros? RPAREN bloque
    ;

parametros
    : tipo ID (COMMA tipo ID)*
    ;

bloque
    : LBRACE sentencia* RBRACE
    ;

// generacion de sentencias (como termina)
sentencia
    : declaracion SEMI      #SDeclaracion
    | asignacion SEMI       #SAsignacion
    | sentenciaControl      #SControl
    | sentenciaIterativa    #SIterativa
    | llamadaFuncion SEMI   #SLlamada
    | impresion SEMI        #SImpresion
    | lectura SEMI          #SLectura
    | retorno SEMI          #SRetorno
    | bloque                #SBloque
    ;

sentenciaControl
    : declaracionIf         #CIf
    | declaracionSwitch     #CSwitch
    ;

sentenciaIterativa
    : declaracionFor            #IFor
    | declaracionWhile          #IWhile
    | declaracionDoWhile        #IDoWhile
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

lectura
    : MORAU LPAREN ID RPAREN
    ;

retorno
    : KUCHIYOSE expresion?
    ;

// Gramatica o Producciones
expresion
    : LPAREN expresion RPAREN                           #EParentesis
    | NOT expresion                                     #ENegacion
    | expresion op=(MULT | DIV) expresion               #EMultiplicativa
    | expresion op=(PLUS | MINUS) expresion             #EAditiva
    | expresion op=(LT | GT | LEQ | GEQ) expresion      #ERelacional
    | expresion op=(EQ | NEQ) expresion                 #EIgualdad
    | expresion op=(AND | OR) expresion                 #ELogica
    | llamadaFuncion                                    #ELlamada
    | ID                                                #EVariable
    | literal                                           #ELiteral
    ;

//llamda a funciones
llamadaFuncion
    : ID LPAREN (expresion (COMMA expresion)*)? RPAREN
    ;

// literales
literal
    : DOUBLE    #LDouble
    | INT       #LInt
    | CHAR      #LChar
    | STRING    #LString
    | MARU      #LTrue
    | BATSU     #LFalse
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
CHAKRA          : 'CHAKRA';         // int
RYO             : 'RYO';            // double
KANA            : 'KANA';           // char
SHINRI          : 'SHINRI';         // boolean
MOJI            : 'MOJI';           // string

MOSHI           : 'MOSHI';          // if
SORE            : 'SORE';           // else
MATA            : 'MATA';           // elseif
NARA            : 'NARA';           // then

HENKA           : 'HENKA';          // switch
REI             : 'REI';            // case
KOWASU          : 'KOWASU';         // break
KYUBI           : 'KYUBI';         // default

KURIKAE         : 'KURIKAE';        // for
NAGARA          : 'NAGARA';         // while
SURU            : 'SURU';           // do while

MU              : 'MU';             // void
MARU            : 'MARU';           // true
BATSU           : 'BATSU';          // false

KAKEMONO        : 'KAKEMONO';       // main
JUTSU           : 'JUTSU';          // function
KUCHIYOSE       : 'KUCHIYOSE';      // return
KAI             : 'KAI';            // print (cout)
MORAU           : 'MORAU';          // read (cin)

LPAREN          : 'KAISHI';
RPAREN          : 'SHURIO';
LBRACE          : 'OUGI';
RBRACE          : 'GOKUI';
SEMI            : 'TEN';
COMMA           : 'MO';
ASSIGN          : 'NARI';
COLON           : 'TSUGI';

PLUS            : 'ZOKA';
MINUS           : 'GENSHO';
MULT            : 'BAI';
DIV             : 'WARU';

LEQ             : 'SAITO';
GEQ             : 'DAITO';
EQ              : 'ONAJI';
NEQ             : 'CHIGAU';

LT              : 'SAI';
GT              : 'DAI';

AND             : 'TO';
OR              : 'MATAWA';
NOT             : 'IE';

DOUBLE          : [0-9]+ '.' [0-9]+;
INT             : [0-9]+;
CHAR            : '\'' (~['\\] | '\\' .) '\'';
STRING          : '"' (~["\\] | '\\' . )* '"';

ID              : [a-zA-Z_][a-zA-Z0-9_]*;
WS              : [ \t\r\n]+ -> skip;

LINE_COMMENT    : '//' ~[\r\n]* -> skip;
BLOCK_COMMENT   : '/*' .*? '*/' -> skip;