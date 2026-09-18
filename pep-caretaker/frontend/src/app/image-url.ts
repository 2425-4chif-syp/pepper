import { environment } from '../environments/environment';

/** URL for displaying a stored picture, resized through imagor when the environment has one. */
export function pictureUrl(imageId: number | string | undefined | null, width = 800): string {
  if (imageId === undefined || imageId === null || imageId === '') return '';
  const path = `image/picture/${imageId}`;
  if (!environment.imagor) return environment.apiUrl + path;
  const source = encodeURIComponent(environment.imagor.sourceBaseUrl + path);
  return `${environment.imagor.url}/unsafe/fit-in/${width}x0/${source}?ngsw-bypass=true`;
}
