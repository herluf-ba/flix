///
/// A trait for division.
///
pub trait Div[a] {
    ///
    /// Returns `x` divided by `y`.
    ///
    pub def div(x: a, y: a): a
}

instance Div[Float32] {
    pub def div(x: Float32, y: Float32): Float32 = $FLOAT32_DIV$(x, y)
}

instance Div[Float64] {
    pub def div(x: Float64, y: Float64): Float64 = $FLOAT64_DIV$
