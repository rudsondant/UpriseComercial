using UnityEngine;
using System.Collections;

public class BallControl : MonoBehaviour {

	public float ballSpeed;

	// Use this for initialization
	void Start () {
		StartCoroutine(Wait (3f));
	}

	IEnumerator Wait(float time)
	{
		yield return new WaitForSeconds(time);		
		GoBall();
	}
	
	// Update is called once per frame
	void OnCollisionEnter2D (Collision2D collInfo) {
		if (collInfo.collider.tag == "Player") {
			rigidbody2D.velocity = new Vector2(rigidbody2D.velocity.x, rigidbody2D.velocity.y/2 + collInfo.collider.rigidbody2D.velocity.y/3);
		}
				
	}

	void ResetBall()	{
		rigidbody2D.velocity = new Vector2 (0f, 0f);
		transform.position = new Vector2 (0f, 0f);
		StartCoroutine(Wait (1f));
		}

	void GoBall(){
				float randomNumber = Random.Range (0, 2);
				if (randomNumber <= 0.5) 
			rigidbody2D.AddForce (new Vector2 (ballSpeed, 10));
				else
			rigidbody2D.AddForce (new Vector2 (-ballSpeed, -10));
		}
}
