# Major and Minor Basilicas — image generation brief

One new image for the "Major and Minor Basilicas" comparison article (Holy
Mass hub, section 19, second article `art.mass.basilica_ranks`). It uses the
same side-by-side "VS" layout as "Basilica or Cathedral?".

| Cell | Asset | Status |
| --- | --- | --- |
| Major Basilica | `mass_basilica.png` | **reused**, already in the app |
| Minor Basilica | `mass_minor_basilica.png` | **needed** |

The major side reuses the existing domed Baroque basilica, so the new image
**must match `mass_basilica.png`**: same scale, same three-quarter view, same
warm cream-and-honey stone, same golden light from the upper left, same
transparent cut-out treatment. Open it beside the new one before accepting.

The contrast between the two is in **scale and setting, not quality**. The
minor basilica is a modest, dignified parish-sized church, the kind of place
a town is proud of. It is not shabby, ruined or rustic.

## Rules

- **Transparent background is required**: a PNG with a real alpha channel.
  At most a thin strip of paving at the base. No sky, clouds, trees, street
  or people. (The source infographic had trees around it; leave them out so
  it pairs with the major basilica.)
- **No text, no labels, no watermark, no signature.**
- Square **1:1, 1024×1024**. The building fills slightly less of the frame
  than the major basilica does, so the pair reads "great" and "smaller".
- Check the alpha channel after generating. Earlier cut-outs arrived with
  the transparency checkerboard baked into the edges.

**Negative prompt:** `text, letters, words, labels, watermark, signature,
sky, clouds, trees, bushes, people, cars, street, landscape, background
scenery, dome, ruins, cartoon, flat vector, low detail`

## `mass_minor_basilica.png`: the minor basilica

An original, generic Italianate church (not a portrait of any real one): a
classical pedimented façade with pilasters, three round-arched entrance
portals, a round window above the central door, a small cross on the
pediment, and a square campanile (bell tower) with an arched belfry rising
to one side. No dome. Two basilica marks appear as discreet details, the
same ones drawn on `mass_basilica.png`: a small carved crossed-keys emblem
above the central door, and a small half-open umbraculum striped in red and
gold beside the entrance.

```
Create an original illustration: a modest Italianate church, three-quarter
view, centered, on a plain transparent background, matching a companion
illustration of a grand domed basilica in angle, light and finish but
smaller in scale. Warm cream-and-honey stone, a classical façade with a
triangular pediment, flat pilasters, three round-arched entrance portals,
a round window above the central portal, a small cross on the peak of the
pediment, and a square bell tower with an arched belfry and small cupola
rising beside the nave on one side. No dome. Over the central door, a small
carved emblem of two crossed keys; beside the entrance, a small half-open
canopy striped in red and gold. Only a thin strip of paving at the base.
Warm golden light from the upper left, fine architectural detail,
dignified and reverent, classical illustration style. No sky, no clouds,
no trees, no people, no text, no labels, no watermark, no signature, no
background at all. Export with a transparent alpha channel. Square 1:1.
```

---

## After generating

Drop the PNG into `docs/image-prompts/mass/` and say so. It will be converted
to WebP in `shared/src/commonMain/composeResources/drawable/`. The content
already references it (`hub/mass/mass_minor_basilica.png`). Until then the
minor-basilica column shows as a name card without a picture.
