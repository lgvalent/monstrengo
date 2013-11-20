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
     * serem usadas no código e evitar erro de digitação. */
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
 * Lista de países
 * 
 * 
 * Segundo a norma ISO3166: Fonte: http://www.inf.ufrgs.br/~cabral/Paises.html
	Afeganistão	AF	AFG	004
	África do Sul	ZA	ZAF	710
	Albânia	AL	ALB	008
	Alemanha	DE	DEU	276
	Algéria	DZ	DZA	012
	Andorra	AD	AND	020
	Angola	AO	AGO	024
	Anguilla	AI	AIA	660
	Antártida	AQ	ATA	010
	Antígua e Barbuda	AG	ATG	028
	Antilhas Holandesas	AN	ANT	530
	Arábia Saudita	SA	SAU	682
	Argentina	AR	ARG	032
	Armênia	AM	ARM	51
	Aruba	AW	ABW	533
	Austrália	AU	AUS	036
	Áustria	AT	AUT	040
	Azerbaijão	AZ 	AZE	31
	Bahamas	BS	BHS	044
	Bahrein	BH	BHR	048
	Bangladesh	BD	BGD	050
	Barbados	BB	BRB	052
	Belarus	BY	BLR	112
	Bélgica	BE	BEL	056
	Belize	BZ	BLZ	084
	Benin	BJ	BEN	204
	Bermudas	BM	BMU	060
	Bolívia	BO	BOL	068
	Bósnia-Herzegóvina	BA	BIH	070
	Botsuana	BW	BWA	072
	Brasil	BR	BRA	076
	Brunei	BN	BRN	096
	Bulgária	BG	BGR	100
	Burkina Fasso	BF	BFA	854
	Burundi	BI	BDI	108
	Butão	BT	BTN	064
	Cabo Verde	CV	CPV	132
	Camarões	CM	CMR	120
	Camboja	KH	KHM	116
	Canadá	CA	CAN	124
	Cazaquistão	KZ	KAZ	398
	Chade	TD	TCD	148
	Chile	CL	CHL	152
	China	CN	CHN	156
	Chipre	CY	CYP	196
	Cingapura	SG	SGP	702
	Colômbia	CO	COL	170
	Congo	CG	COG	178
	Coréia do Norte	KP	PRK	408
	Coréia do Sul	KR	KOR	410
	Costa do Marfim	CI	CIV	384
	Costa Rica	CR	CRI	188
	Croácia (Hrvatska)	HR	HRV	191
	Cuba	CU	CUB	192
	Dinamarca	DK	DNK	208
	Djibuti	DJ	DJI	262
	Dominica	DM	DMA	212
	Egito	EG	EGY	818
	El Salvador	SV	SLV	222
	Emirados Árabes Unidos	AE	ARE	784
	Equador	EC	ECU	218
	Eritréia	ER	ERI	232
	Eslováquia	SK	SVK	703
	Eslovênia	SI	SVN	705
	Espanha	ES	ESP	724
	Estados Unidos	US	USA	840
	Estônia	EE	EST	233
	Etiópia	ET	ETH	231
	Federação Russa	RU	RUS	643
	Fiji	FJ	FJI	242
	Filipinas	PH	PHL	608
	Finlândia	FI	FIN	246
	França	FR	FRA	250
	França Metropolitana	FX	FXX	249
	Gabão	GA	GAB	266
	Gâmbia	GM	GMB	270
	Gana	GH	GHA	288
	Geórgia	GE	GEO	268
	Gibraltar	GI	GIB	292
	Grã-Bretanha (Reino Unido, UK)	GB	GBR	826
	Granada	GD	GRD	308
	Grécia	GR	GRC	300
	Groelândia	GL	GRL	304
	Guadalupe	GP	GLP	312
	Guam (Território dos Estados Unidos)	GU	GUM	316
	Guatemala	GT	GTM	320
	Guiana	GY	GUY	328
	Guiana Francesa	GF	GUF	254
	Guiné	GN	GIN	324
	Guiné Equatorial	GQ	GNQ	226
	Guiné-Bissau	GW	GNB	624
	Haiti	HT	HTI	332
	Holanda	NL	NLD	528
	Honduras	HN	HND	340
	Hong Kong	HK	HKG	344
	Hungria	HU	HUN	348
	Iêmen	YE	YEM	887
	Ilha Bouvet (Território da Noruega)	BV	BVT	074
	Ilha Natal	CX	CXR	162
	Ilha Pitcairn	PN	PCN	612
	Ilha Reunião	RE	REU	638
	Ilhas Cayman	KY	CYM	136
	Ilhas Cocos	CC	CCK	166
	Ilhas Comores	KM	COM	174
	Ilhas Cook	CK	COK	184
	Ilhas Faeroes	FO	FRO	234
	Ilhas Falkland (Malvinas)	FK	FLK	238
	Ilhas Geórgia do Sul e Sandwich do Sul	GS	SGS	239
	Ilhas Heard e McDonald (Território da Austrália)	HM	HMD	334
	Ilhas Marianas do Norte	MP	MNP	580
	Ilhas Marshall	MH	MHL	584
	Ilhas Menores dos Estados Unidos	UM	UMI	581
	Ilhas Norfolk	NF	NFK	574
	Ilhas Seychelles	SC	SYC	690
	Ilhas Solomão	SB	SLB	090
	Ilhas Svalbard e Jan Mayen	SJ	SJM	744
	Ilhas Tokelau	TK	TKL	772
	Ilhas Turks e Caicos	TC	TCA	796
	Ilhas Virgens (Estados Unidos)	VI	VIR	850
	Ilhas Virgens (Inglaterra)	VG	VGB	092
	Ilhas Wallis e Futuna	WF	WLF	876
	índia	IN	IND	356
	Indonésia	ID	IDN	360
	Irã	IR	IRN	364
	Iraque	IQ	IRQ	368
	Irlanda	IE	IRL	372
	Islândia	IS	ISL	352
	Israel	IL	ISR	376
	Itália	IT	ITA	380
	Iugoslávia	YU	YUG	891
	Jamaica	JM	JAM	388
	Japão	JP	JPN	392
	Jordânia	JO	JOR	400
	Kênia	KE	KEN	404
	Kiribati	KI	KIR	296
	Kuait	KW	KWT	414
	Laos	LA	LAO	418
	Látvia	LV	LVA	428
	Lesoto	LS	LSO	426
	Líbano	LB	LBN	422
	Libéria	LR	LBR	430
	Líbia	LY	LBY	434
	Liechtenstein	LI	LIE	438
	Lituânia	LT	LTU	440
	Luxemburgo	LU	LUX	442
	Macau	MO	MAC	446
	Macedônia	MK	MKD	807
	Madagascar	MG	MDG	450
	Malásia	MY	MYS	458
	Malaui	MW	MWI	454
	Maldivas	MV	MDV	462
	Mali	ML	MLI	466
	Malta	MT	MLT	470
	Marrocos	MA	MAR	504
	Martinica	MQ	MTQ	474
	Maurício	MU	MUS	480
	Mauritânia	MR	MRT	478
	Mayotte	YT	MYT	175
	México	MX	MEX	484
	Micronésia	FM	FSM	583
	Moçambique	MZ	MOZ	508
	Moldova	MD	MDA	498
	Mônaco	MC	MCO	492
	Mongólia	MN	MNG	496
	Montserrat	MS	MSR	500
	Myanma	MM	MMR	104
	Namíbia	NA	NAM	516
	Nauru	NR	NRU	520
	Nepal	NP	NPL	524
	Nicarágua	NI	NIC	558
	Níger	NE	NER	562
	Nigéria	NG	NGA	566
	Niue	NU	NIU	570
	Noruega	NO	NOR	578
	Nova Caledônia	NC	NCL	540
	Nova Zelândia	NZ	NZL	554
	Omã	OM	OMN	512
	Palau	PW	PLW	585
	Panamá	PA	PAN	591
	Papua-Nova Guiné	PG	PNG	598
	Paquistão	PK	PAK	586
	Paraguai	PY	PRY	600
	Peru	PE	PER	604
	Polinésia Francesa	PF	PYF	258
	Polônia	PL	POL	616
	Porto Rico	PR	PRI	630
	Portugal	PT	PRT	620
	Qatar	QA	QAT	634
	Quirguistão	KG	KGZ	417
	República Centro-Africana	CF	CAF	140
	República Dominicana	DO	DOM	214
	República Tcheca	CZ	CZE	203
	Romênia	RO	ROM	642
	Ruanda	RW	RWA	646
	Saara Ocidental	EH	ESH	732
	Saint Vincente e Granadinas	VC	VCT	670
	Samoa Ocidental	AS	ASM	016
	Samoa Ocidental	WS	WSM	882
	San Marino	SM	SMR	674
	Santa Helena	SH	SHN	654
	Santa Lúcia	LC	LCA	662
	São Cristóvão e Névis	KN	KNA	659
	São Tomé e Príncipe	ST	STP	678
	Senegal	SN	SEN	686
	Serra Leoa	SL	SLE	694
	Síria	SY	SYR	760
	Somália	SO	SOM	706
	Sri Lanka	LK	LKA	144
	St. Pierre and Miquelon	PM	SPM	666
	Suazilândia	SZ	SWZ	748
	Sudão	SD	SDN	736
	Suécia	SE	SWE	752
	Suíça	CH	CHE	756
	Suriname	SR	SUR	740
	Tadjiquistão	TJ	TJK	762
	Tailândia	TH	THA	764
	Taiwan	TW	TWN	158
	Tanzânia	TZ	TZA	834
	Território Britânico do Oceano índico	IO	IOT	086
	Territórios do Sul da França	TF	ATF	260
	Timor Leste	TP	TMP	626
	Togo	TG	TGO	768
	Tonga	TO	TON	776
	Trinidad and Tobago	TT	TTO	780
	Tunísia	TN	TUN	788
	Turcomenistão	TM	TKM	795
	Turquia	TR	TUR	792
	Tuvalu	TV	TUV	798
	Ucrânia	UA	UKR	804
	Uganda	UG	UGA	800
	Uruguai	UY	URY	858
	Uzbequistão	UZ	UZB	860
	Vanuatu	VU	VUT	548
	Vaticano	VA	VAT	336
	Venezuela	VE	VEN	862
	Vietnã	VN	VNM	704
	Zaire	ZR	ZAR	180
	Zâmbia	ZM	ZMB	894
	Zimbábue	ZW	ZWE	716
 */