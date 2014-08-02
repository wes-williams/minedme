package com.minedme.social.util;

public class ScoreUtil
{

	public static byte score(int scoreCount)
	{
		byte score = 0;
		
		if(scoreCount > 1)
		{
			score += 1;

			if(scoreCount > 3)
			{
				score += 3;
				
				if(scoreCount > 5)
				{
					score += 6;

					if(scoreCount > 10)
					{
						score += 10;

						if(scoreCount > 20)
						{
							score += 15;

							if(scoreCount > 40)
							{
								score += 30;

								if(scoreCount > 80)
								{
									score += 60;
								}
							}
						}
					}
				}
			}
		}

		return score;
	}
}
