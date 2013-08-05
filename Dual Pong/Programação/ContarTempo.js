var tempomax : float = 5;
var acabouTempo : boolean = false;
function Update () {
	ContarTempo();
	if(acabouTempo)
		//Teste transform.position.x = 100;
}

function ContarTempo(){
	tempomax -= Time.deltaTime;
	if (tempomax <= 0)
		acabouTempo = true;	
}