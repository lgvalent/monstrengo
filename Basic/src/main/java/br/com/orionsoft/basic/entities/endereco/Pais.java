package br.com.orionsoft.basic.entities.endereco;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Lucio 20071129
 */
@Entity
@Table(name="basic_pais")
public class Pais
{
    /* Constantes com o nomes das propriedades da classe para
     * serem usadas no c�digo e evitar erro de digita��o. */
    public static final String NOME = "nome";
    public static final String SIGLA2 = "sigla2";
    public static final String SIGLA3 = "sigla3";

    private long id = -1;
    private String nome;
    private String sigla2;
    private String sigla3;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId(){return id;}
    public void setId(long id){this.id = id;}

    @Column(length=50)
    public String getNome(){return nome;}
    public void setNome(String nome){this.nome = nome;}

    @Column(length=2)
    public String getSigla2(){return sigla2;}
    public void setSigla2(String sigla2){this.sigla2 = sigla2;}

    @Column(length=3)
    public String getSigla3(){return sigla3;}
    public void setSigla3(String sigla3){this.sigla3 = sigla3;}

    public String toString()
    {
        return this.nome;
    }

}

/**
 * Lista de pa�ses
 * 
 * 
 * Segundo a norma ISO3166: Fonte: http://www.inf.ufrgs.br/~cabral/Paises.html
	Afeganist�o	AF	AFG	004
	�frica do Sul	ZA	ZAF	710
	Alb�nia	AL	ALB	008
	Alemanha	DE	DEU	276
	Alg�ria	DZ	DZA	012
	Andorra	AD	AND	020
	Angola	AO	AGO	024
	Anguilla	AI	AIA	660
	Ant�rtida	AQ	ATA	010
	Ant�gua e Barbuda	AG	ATG	028
	Antilhas Holandesas	AN	ANT	530
	Ar�bia Saudita	SA	SAU	682
	Argentina	AR	ARG	032
	Arm�nia	AM	ARM	51
	Aruba	AW	ABW	533
	Austr�lia	AU	AUS	036
	�ustria	AT	AUT	040
	Azerbaij�o	AZ 	AZE	31
	Bahamas	BS	BHS	044
	Bahrein	BH	BHR	048
	Bangladesh	BD	BGD	050
	Barbados	BB	BRB	052
	Belarus	BY	BLR	112
	B�lgica	BE	BEL	056
	Belize	BZ	BLZ	084
	Benin	BJ	BEN	204
	Bermudas	BM	BMU	060
	Bol�via	BO	BOL	068
	B�snia-Herzeg�vina	BA	BIH	070
	Botsuana	BW	BWA	072
	Brasil	BR	BRA	076
	Brunei	BN	BRN	096
	Bulg�ria	BG	BGR	100
	Burkina Fasso	BF	BFA	854
	Burundi	BI	BDI	108
	But�o	BT	BTN	064
	Cabo Verde	CV	CPV	132
	Camar�es	CM	CMR	120
	Camboja	KH	KHM	116
	Canad�	CA	CAN	124
	Cazaquist�o	KZ	KAZ	398
	Chade	TD	TCD	148
	Chile	CL	CHL	152
	China	CN	CHN	156
	Chipre	CY	CYP	196
	Cingapura	SG	SGP	702
	Col�mbia	CO	COL	170
	Congo	CG	COG	178
	Cor�ia do Norte	KP	PRK	408
	Cor�ia do Sul	KR	KOR	410
	Costa do Marfim	CI	CIV	384
	Costa Rica	CR	CRI	188
	Cro�cia (Hrvatska)	HR	HRV	191
	Cuba	CU	CUB	192
	Dinamarca	DK	DNK	208
	Djibuti	DJ	DJI	262
	Dominica	DM	DMA	212
	Egito	EG	EGY	818
	El Salvador	SV	SLV	222
	Emirados �rabes Unidos	AE	ARE	784
	Equador	EC	ECU	218
	Eritr�ia	ER	ERI	232
	Eslov�quia	SK	SVK	703
	Eslov�nia	SI	SVN	705
	Espanha	ES	ESP	724
	Estados Unidos	US	USA	840
	Est�nia	EE	EST	233
	Eti�pia	ET	ETH	231
	Federa��o Russa	RU	RUS	643
	Fiji	FJ	FJI	242
	Filipinas	PH	PHL	608
	Finl�ndia	FI	FIN	246
	Fran�a	FR	FRA	250
	Fran�a Metropolitana	FX	FXX	249
	Gab�o	GA	GAB	266
	G�mbia	GM	GMB	270
	Gana	GH	GHA	288
	Ge�rgia	GE	GEO	268
	Gibraltar	GI	GIB	292
	Gr�-Bretanha (Reino Unido, UK)	GB	GBR	826
	Granada	GD	GRD	308
	Gr�cia	GR	GRC	300
	Groel�ndia	GL	GRL	304
	Guadalupe	GP	GLP	312
	Guam (Territ�rio dos Estados Unidos)	GU	GUM	316
	Guatemala	GT	GTM	320
	Guiana	GY	GUY	328
	Guiana Francesa	GF	GUF	254
	Guin�	GN	GIN	324
	Guin� Equatorial	GQ	GNQ	226
	Guin�-Bissau	GW	GNB	624
	Haiti	HT	HTI	332
	Holanda	NL	NLD	528
	Honduras	HN	HND	340
	Hong Kong	HK	HKG	344
	Hungria	HU	HUN	348
	I�men	YE	YEM	887
	Ilha Bouvet (Territ�rio da Noruega)	BV	BVT	074
	Ilha Natal	CX	CXR	162
	Ilha Pitcairn	PN	PCN	612
	Ilha Reuni�o	RE	REU	638
	Ilhas Cayman	KY	CYM	136
	Ilhas Cocos	CC	CCK	166
	Ilhas Comores	KM	COM	174
	Ilhas Cook	CK	COK	184
	Ilhas Faeroes	FO	FRO	234
	Ilhas Falkland (Malvinas)	FK	FLK	238
	Ilhas Ge�rgia do Sul e Sandwich do Sul	GS	SGS	239
	Ilhas Heard e McDonald (Territ�rio da Austr�lia)	HM	HMD	334
	Ilhas Marianas do Norte	MP	MNP	580
	Ilhas Marshall	MH	MHL	584
	Ilhas Menores dos Estados Unidos	UM	UMI	581
	Ilhas Norfolk	NF	NFK	574
	Ilhas Seychelles	SC	SYC	690
	Ilhas Solom�o	SB	SLB	090
	Ilhas Svalbard e Jan Mayen	SJ	SJM	744
	Ilhas Tokelau	TK	TKL	772
	Ilhas Turks e Caicos	TC	TCA	796
	Ilhas Virgens (Estados Unidos)	VI	VIR	850
	Ilhas Virgens (Inglaterra)	VG	VGB	092
	Ilhas Wallis e Futuna	WF	WLF	876
	�ndia	IN	IND	356
	Indon�sia	ID	IDN	360
	Ir�	IR	IRN	364
	Iraque	IQ	IRQ	368
	Irlanda	IE	IRL	372
	Isl�ndia	IS	ISL	352
	Israel	IL	ISR	376
	It�lia	IT	ITA	380
	Iugosl�via	YU	YUG	891
	Jamaica	JM	JAM	388
	Jap�o	JP	JPN	392
	Jord�nia	JO	JOR	400
	K�nia	KE	KEN	404
	Kiribati	KI	KIR	296
	Kuait	KW	KWT	414
	Laos	LA	LAO	418
	L�tvia	LV	LVA	428
	Lesoto	LS	LSO	426
	L�bano	LB	LBN	422
	Lib�ria	LR	LBR	430
	L�bia	LY	LBY	434
	Liechtenstein	LI	LIE	438
	Litu�nia	LT	LTU	440
	Luxemburgo	LU	LUX	442
	Macau	MO	MAC	446
	Maced�nia	MK	MKD	807
	Madagascar	MG	MDG	450
	Mal�sia	MY	MYS	458
	Malaui	MW	MWI	454
	Maldivas	MV	MDV	462
	Mali	ML	MLI	466
	Malta	MT	MLT	470
	Marrocos	MA	MAR	504
	Martinica	MQ	MTQ	474
	Maur�cio	MU	MUS	480
	Maurit�nia	MR	MRT	478
	Mayotte	YT	MYT	175
	M�xico	MX	MEX	484
	Micron�sia	FM	FSM	583
	Mo�ambique	MZ	MOZ	508
	Moldova	MD	MDA	498
	M�naco	MC	MCO	492
	Mong�lia	MN	MNG	496
	Montserrat	MS	MSR	500
	Myanma	MM	MMR	104
	Nam�bia	NA	NAM	516
	Nauru	NR	NRU	520
	Nepal	NP	NPL	524
	Nicar�gua	NI	NIC	558
	N�ger	NE	NER	562
	Nig�ria	NG	NGA	566
	Niue	NU	NIU	570
	Noruega	NO	NOR	578
	Nova Caled�nia	NC	NCL	540
	Nova Zel�ndia	NZ	NZL	554
	Om�	OM	OMN	512
	Palau	PW	PLW	585
	Panam�	PA	PAN	591
	Papua-Nova Guin�	PG	PNG	598
	Paquist�o	PK	PAK	586
	Paraguai	PY	PRY	600
	Peru	PE	PER	604
	Polin�sia Francesa	PF	PYF	258
	Pol�nia	PL	POL	616
	Porto Rico	PR	PRI	630
	Portugal	PT	PRT	620
	Qatar	QA	QAT	634
	Quirguist�o	KG	KGZ	417
	Rep�blica Centro-Africana	CF	CAF	140
	Rep�blica Dominicana	DO	DOM	214
	Rep�blica Tcheca	CZ	CZE	203
	Rom�nia	RO	ROM	642
	Ruanda	RW	RWA	646
	Saara Ocidental	EH	ESH	732
	Saint Vincente e Granadinas	VC	VCT	670
	Samoa Ocidental	AS	ASM	016
	Samoa Ocidental	WS	WSM	882
	San Marino	SM	SMR	674
	Santa Helena	SH	SHN	654
	Santa L�cia	LC	LCA	662
	S�o Crist�v�o e N�vis	KN	KNA	659
	S�o Tom� e Pr�ncipe	ST	STP	678
	Senegal	SN	SEN	686
	Serra Leoa	SL	SLE	694
	S�ria	SY	SYR	760
	Som�lia	SO	SOM	706
	Sri Lanka	LK	LKA	144
	St. Pierre and Miquelon	PM	SPM	666
	Suazil�ndia	SZ	SWZ	748
	Sud�o	SD	SDN	736
	Su�cia	SE	SWE	752
	Su��a	CH	CHE	756
	Suriname	SR	SUR	740
	Tadjiquist�o	TJ	TJK	762
	Tail�ndia	TH	THA	764
	Taiwan	TW	TWN	158
	Tanz�nia	TZ	TZA	834
	Territ�rio Brit�nico do Oceano �ndico	IO	IOT	086
	Territ�rios do Sul da Fran�a	TF	ATF	260
	Timor Leste	TP	TMP	626
	Togo	TG	TGO	768
	Tonga	TO	TON	776
	Trinidad and Tobago	TT	TTO	780
	Tun�sia	TN	TUN	788
	Turcomenist�o	TM	TKM	795
	Turquia	TR	TUR	792
	Tuvalu	TV	TUV	798
	Ucr�nia	UA	UKR	804
	Uganda	UG	UGA	800
	Uruguai	UY	URY	858
	Uzbequist�o	UZ	UZB	860
	Vanuatu	VU	VUT	548
	Vaticano	VA	VAT	336
	Venezuela	VE	VEN	862
	Vietn�	VN	VNM	704
	Zaire	ZR	ZAR	180
	Z�mbia	ZM	ZMB	894
	Zimb�bue	ZW	ZWE	716
 */